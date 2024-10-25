#!/bin/bash

# Set the library path for native libraries
export LD_LIBRARY_PATH=project/libsysinfo:$LD_LIBRARY_PATH

java -Djava.library.path=project/libsysinfo -cp "project:lib/jfreechart-1.0.13.jar:lib/jcommon-1.0.23.jar" Main