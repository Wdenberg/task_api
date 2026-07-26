# ==========================================
# ESTÁGIO 1: Build da Aplicação (Com Maven + JDK 21)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copia os arquivos de configuração do Maven e o pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dá permissão de execução no wrapper do Maven e baixa as dependências (Layer Caching)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte para dentro do container
COPY src src

# Executa o build gerando o arquivo .jar (ignorando testes apenas no docker build, pois já rodamos na CI)
RUN ./mvnw clean package -DskipTests

# ==========================================
# ESTÁGIO 2: Runtime da Aplicação (Apenas com JRE 21 leve)
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Cria um usuário não-root por questões de segurança (Boas práticas DevOps/SecOps)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia apenas o .jar gerado no Estágio 1 para dentro desta imagem final enxuta
COPY --from=build /workspace/app/target/*.jar app.jar

# Expõe a porta configurada no nosso Spring Boot
EXPOSE 8080

# Parâmetros de otimização de memória nativos do Java moderno para containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]