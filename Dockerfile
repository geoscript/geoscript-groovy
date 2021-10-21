FROM groovy:jre8
COPY target/geoscript-groovy-1.18.0-app/geoscript-groovy-1.18.0 /usr/src/geoscript-groovy
WORKDIR /usr/src/geoscript-groovy
CMD ["bin/geoscript-groovysh"]