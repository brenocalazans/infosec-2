#!/bin/bash
cd src
javac DigestCalculator.java
java DigestCalculator SHA1 ../DigitalSignatureExample.java ../SymmetricKeyCipherExample.java ../DigitalSignatureExample\(copy\).java ../lista_digests.txt
cd ..
