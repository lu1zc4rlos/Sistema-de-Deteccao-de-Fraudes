import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from sklearn.preprocessing import StandardScaler
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

# ==========================================
# 1. GERAÇÃO E ENGENHARIA DE RECURSOS (DATASET)
# ==========================================
np.random.seed(42) # Mantém os resultados reproduzíveis
n_samples = 10000

# Criando 4 Features (Variáveis de entrada)
amount = np.random.exponential(scale=150, size=n_samples) # Valores de transação
is_foreign = np.random.choice([0.0, 1.0], size=n_samples, p=[0.9, 0.1]) # 10% transações fora do BR
is_unknown_device = np.random.choice([0.0, 1.0], size=n_samples, p=[0.85, 0.15]) # 15% dispositivos novos
hour_of_day = np.random.randint(0, 24, size=n_samples).astype(float) # Hora da transação (0 a 23)

# Regra heurística oculta para gerar o rótulo (Target): 1 = Fraude, 0 = Legítima
fraud_score_simulated = (
    (amount > 800).astype(int) * 0.4 +
    is_foreign * 0.3 +
    is_unknown_device * 0.2 +
    ((hour_of_day < 6) | (hour_of_day > 23)).astype(int) * 0.3
)
# Define como fraude se a probabilidade acumulada for superior ao ponto de corte
is_fraud = (fraud_score_simulated + np.random.normal(0, 0.05, n_samples) > 0.5).astype(int)

# Empacotando em uma matriz de entradas X e vetor target y
X = np.column_stack((amount, is_foreign, is_unknown_device, hour_of_day))
y = is_fraud

# ==========================================
# 2. DIVISÃO E NORMALIZAÇÃO DOS DADOS
# ==========================================
# 80% dos dados para a rede aprender / 20% para testar a precisão
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Redes neurais são sensíveis à escala! (ex: amount varia de 0 a 5000, hour varia de 0 a 23)
# O StandardScaler ajusta os dados para média 0 e desvio padrão 1.
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# ==========================================
# 3. CONSTRUÇÃO E TREINAMENTO DA REDE NEURAL
# ==========================================
# Instanciando uma MLP:
# - hidden_layer_sizes=(16, 8): Duas camadas ocultas com 16 e 8 neurônios
# - activation='relu': Função de ativação ReLU nas camadas ocultas
# - max_iter=500: Número máximo de épocas (passadas pelos dados)
mlp = MLPClassifier(hidden_layer_sizes=(16, 8), activation='relu', max_iter=500, random_state=42)
mlp.fit(X_train_scaled, y_train)

# Avaliando o desempenho do modelo nos dados de teste
accuracy = mlp.score(X_test_scaled, y_test)
print(f"🎯 Acurácia do Modelo nos dados de teste: {accuracy * 100:.2f}%")

# ==========================================
# 4. EXPORTAÇÃO PARA FORMATO ONNX
# ==========================================
# Definindo a forma da entrada esperada pelo ONNX: Tensor de Floats com 4 colunas
initial_type = [('float_input', FloatTensorType([None, 4]))]

# Convertendo o modelo (Nota: Em produção completa, o Scaler também é exportado ou replicado no Java)
onnx_model = convert_sklearn(mlp, initial_types=initial_type)

# Salvando o arquivo final do modelo
with open("fraud_model.onnx", "wb") as f:
    f.write(onnx_model.SerializeToString())

print("💾 Modelo exportado com sucesso como 'fraud_model.onnx'!")