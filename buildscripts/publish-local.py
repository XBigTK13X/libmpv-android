#! /usr/bin/python3

import sys
import shutil
import os

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

built_arr_path = f'./aar-output/android-libmpv-{VERSION}.aar'
shutil.copy('./aar-output/libmpv.aar',built_arr_path)

built_pom_path = f'./aar-output/android-libmpv-{VERSION}.pom'
with open(built_pom_path,'w',encoding="utf-8") as write_handle:
    write_handle.write(pom)

repo_dir = f'/home/kretst/maven-repo/com/libmpv/android-libmpv/{VERSION}'
os.makedirs(repo_dir,exist_ok=True)

repo_arr_path = os.path.join(repo_dir,f'android-libmpv-{VERSION}.aar')
repo_pom_path = os.path.join(repo_dir,f'android-libmpv-{VERSION}.pom')

shutil.copy(built_arr_path, repo_arr_path)
shutil.copy(built_pom_path, repo_pom_path)

print(f"Version {VERSION} published to local maven repo")