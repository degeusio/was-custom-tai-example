FROM websphere-liberty:full

# Add the app
COPY --chown=1001:0  javaee-app/target/javaee-app.war /config/dropins/

# Add the custom tai, simply in tmp here (referenced in server.xml)
COPY --chown=1001:0  custom-tai/target/custom-tai.jar /tmp/

COPY --chown=1001:0  docker/server.xml /config/

RUN configure.sh
