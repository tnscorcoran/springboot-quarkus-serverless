FROM registry.access.redhat.com/ubi8/ubi-minimal:8.1
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work /work/application
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]