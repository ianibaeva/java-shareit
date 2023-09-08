FROM amazoncorretto:11-alpine-jdk
ENV TZ=Europe/Moscow
COPY target/*.jar gateway.jar
RUN apk update && \
    apk add --no-cache tzdata && \
    ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone
ENTRYPOINT ["java","-jar","/gateway.jar"]
