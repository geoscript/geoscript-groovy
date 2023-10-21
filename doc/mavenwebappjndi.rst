    .. _mavenwebappjndi:

Create GeoScript Web App with JNDI and Maven
============================================

Create a web app Project::

    mvn archetype:generate -B -DgroupId=org.geo -DartifactId=geo-web -DarchetypeArtifactId=maven-archetype-webapp

Move into the new directory::

    cd geo-web

Add Tomcat Plugin to the **pom.xml** file:

.. code-block:: xml

    <plugins>
        <plugin>
          <groupId>org.apache.tomcat.maven</groupId>
          <artifactId>tomcat7-maven-plugin</artifactId>
          <version>2.2</version>
        </plugin>
    </plugins>

Add geoscript as a dependency in the **pom.xml** file:

.. code-block:: xml

    <repositories>
        <repository>
            <id>boundless</id>
            <name>Boundless Maven Repository</name>
            <url>https://repo.boundlessgeo.com/main</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

    <depenencies>
        <dependency>
            <groupId>org.geoscript</groupId>
            <artifactId>geoscript-groovy</artifactId>
            <version>1.21.0</version>
        </dependency>
    <depenencies>

Set up groovlets by adding a servlet to the **src/main/webapp/WEB-INF/web.xml** file:

.. code-block:: xml

    <servlet>
        <servlet-name>Groovy</servlet-name>
        <servlet-class>groovy.servlet.GroovyServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>Groovy</servlet-name>
        <url-pattern>*.groovy</url-pattern>
    </servlet-mapping>

Now register a JNDI datasource to a PostGIS database.

First, create the **src/main/webapp/META-INF** directory::

    mkdir src/main/webapp/META-INF

Next, create a **src/main/webapp/META-INF/context.xml** file with a connection to our database:

.. code-block:: xml

    <Context>
      <Resource name="jdbc/geoscript"
             auth="Container"
             type="javax.sql.DataSource"
             username="your_user_name_here"
             password="super_secret_password_here"
             driverClassName="org.postgresql.Driver"
             url="jdbc:postgresql://localhost:5432/states"
             maxActive="2"
             maxIdle="2"/>
    </Context>

Then, tell our web app about this datasource by registering it with our **src/main/webapp/WEB-INF/web.xml** file:

.. code-block:: xml

    <resource-ref>
        <res-ref-name>jdbc/datasourcename</res-ref-name>
        <res-type>javax.sql.DataSource</res-type>
        <res-auth>Container</res-auth>
    </resource-ref>

Finally, we need to add the Postgres dependency to the tomcat maven plugin in the **pom.xml** file:

.. code-block:: xml

    <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.2</version>
        <dependencies>
            <dependency>
              <groupId>postgresql</groupId>
              <artifactId>postgressql</artifactId>
              <version>8.4-701.jdbc2</version>
            </dependency>
        </dependencies>
    </plugin>

Now that we have our JNDI datasource set up, let's write a Groovlet called **src/main/webapp/layers.groovy**:

.. code-block:: groovy

    import geoscript.workspace.PostGIS

    PostGIS postgis = new PostGIS("java:comp/env/jdbc/geoscript", schema: "public")

    html.html {
        head {
            title("Layers")
        }
        body {
            h1("Layers")
            ul {
                postgis.names.each { name ->
                    li("${name}")
                }
            }
        }
    }

Let's run the app::

    mvn clean install tomcat7:run

And open the following url in our web browser::

    http://localhost:8080/geo-web/layers.groovy

In the previous example we used PostGIS, but we can also use GeoDB, a spatially enabled H2 database.

First, use git to clone the geodb project::

    git clone https://github.com/jdeolive/geodb.git

Then, move into the directory::

    cd geodb

And build the project with maven::

    mvn clean install assembly:assembly

Unzip the compiled and assembled zip file::

    unzip target/geodb-0-SNAPSHOT-app.zip

And then run geodb to start a spatially enabled H2 server::

    geodb-0-SNAPSHOT/bin/geodb -w

Add A resource to the **src/main/webapp/META-INF/context.xml** file:

.. code-block:: xml

    <Resource name="jdbc/h2"
      auth="Container"
      type="javax.sql.DataSource"
      driverClassName="org.h2.Driver"
      url="jdbc:h2:tcp://localhost/test"
      username="sa"
      password=""
      maxActive="2"
      maxIdle="2"/>

and a resource-ref to **src/main/webapp/WEB-INF/web.xml** file:

.. code-block:: xml

    <resource-ref>
        <res-ref-name>jdbc/h2</res-ref-name>
        <res-type>javax.sql.DataSource</res-type>
        <res-auth>Container</res-auth>
    </resource-ref>

Add an H2 dependency to the tomcat plugin in the **pom.xml** file:

.. code-block:: xml

    <plugin>
        <groupId>org.apache.tomcat.maven</groupId>
        <artifactId>tomcat7-maven-plugin</artifactId>
        <version>2.2</version>
        <dependencies>
            <dependency>
              <groupId>com.h2database</groupId>
              <artifactId>h2</artifactId>
              <version>1.1.119</version>
            </dependency>
        </dependencies>
    </plugin>

Add a new groovylet **src/main/webapp/h2.groovy**:

.. code-block:: groovy

    import geoscript.workspace.H2

    H2 h2 = new H2("java:comp/env/jdbc/h2")

    html.html {
        head {
            title("H2 Layers")
        }
        body {
            h1("H2 Layers")
            ul {
                h2.names.each { name ->
                    li("${name}")
                }
            }
            table (border: 1) {
                th("geometry")
                h2["SPATIAL"].eachFeature { f ->
                    tr {
                        td("${f.geom}")
                    }
                }
            }
        }
    }

Let's run the app::

    mvn clean install tomcat7:run

And open the following url in our web browser::

    http://localhost:8080/geo-web/h2.groovy
