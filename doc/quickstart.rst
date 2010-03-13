.. _quickstart:

Quick Start
===========

Install Java
------------

A Java Runtime Environment (JRE), version greater than *1.5*, is required to run Groovy and GeoScript. Chances are your system already has a JRE installed on it. A quick way to test is to execute the following from the command line::

   % java -version
   java version "1.5.0_20"
   Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_20-b02-315)
   Java HotSpot(TM) Client VM (build 1.5.0_20-141, mixed mode, sharing)

If the command is not found or the Java version is less than 1.5 you must install a new JRE. Otherwise you can continue to the :ref:`next step <install_groovy>`.

A JRE can be downloaded from `Sun Microsystems <http://java.sun.com/javase/downloads/index.jsp>`_.

  .. note:: It is possible to run GeoScript with a different non Sun JRE. However the Sun JRE is recommended as it has been thoroughly tested.

.. _install_groovy:

Install Groovy
--------------

Groovy version greater than *1.7* is required for GeoScript. The current version can be downloaded from http://groovy.codehaus.org/.

Install GeoScript
-----------------

#. Download `GeoScript <http://github.com/downloads/jericks/geoscript-groovy/geoscript-groovy0.6.zip>`_

#. Unpack the GeoScript tarball::

     unzip geoscript-groovy0.6.zip

#. Add the geoscript-groovy0.6\bin directory to your path::

     export PATH=geoscript-groovy0.6\bin:$PATH

That's it. GeoScript Groovy should now be installed on the system. To verify the install execute the :command:`geoscript-groovysh` command::

      % geoscript-groovysh
      Groovy Shell (1.7.0, JVM: 1.6.0_17)
      Type 'help' or '\h' for help.
      -----------------------------------------------------------------------------------------------
      groovy:000> import geoscript.geom.Point
      ===> [import geoscript.geom.Point]
      groovy:000> p = new Point(10,10)
      ===> POINT (10 10)

If you do not get an import error congratulations! GeoScript Groovy is installed on the system.
