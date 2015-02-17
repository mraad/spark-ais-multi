#!/bin/sh
docker run\
 --name spark\
 --link mysql:mysql\
 -i -t -P\
 mraad/spark /bin/bash
