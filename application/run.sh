#!/bin/bash

java -jar build/libs/application-all.jar  --enableStreamingMode --questionFile $1 --responseFile $2