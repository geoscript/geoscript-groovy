FROM groovy:jre8
COPY target/geoscript-groovy-1.18-SNAPSHOT-app/geoscript-groovy-1.18-SNAPSHOT /usr/src/geoscript-groovy
WORKDIR /usr/src/geoscript-groovy
CMD ["bin/geoscript-groovysh"]