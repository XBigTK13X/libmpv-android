#! /usr/bin/python3

import sys
import shutil

if len(sys.argv) < 2:
    print("Version required")
    sys.exit(1)

VERSION = sys.argv[1]

pom = f'''<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.libmpv</groupId>
  <artifactId>android-libmpv</artifactId>
  <version>{VERSION}</version>
  <packaging>aar</packaging>
</project>
'''

shutil.copy('./aar-output/libmpv.aar',f'./aar-output/android-libmpv-{VERSION}.aar')

with open(f'./aar-output/android-libmpv-{VERSION}.pom','w',encoding="utf-8") as write_handle:
    write_handle.write(pom)

print(f"Upload reposlite artifacts to\n\tmain/com/libmpv/android-libmpv/{VERSION}")