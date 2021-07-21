#!/bin/bash

# welcome message
echo "Secret Creator - Press Enter To Continue"
read -r

#Get the Nexus URL
echo "Enter your api key"
read -r apikey

echo ""

#Get the Nexus Username
echo "Enter your hash"
read -r hash

APF=".secrets"
touch $APF

echo "apikey=\"$apikey\"" > $APF
echo "hash=\"$hash\"" >> $APF

echo ""
echo "Secrets file created successfully!"




