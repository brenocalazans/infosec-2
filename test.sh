#!/bin/bash
cd src
javac DigestCalculator.java
echo colisao por linha de comando
java DigestCalculator SHA1 ../DigitalSignatureExample.java ../README.md ../SymmetricKeyCipherExample.java ../DigitalSignatureExample\(copy\).java ../lista_digests.txt

echo
echo sem colisao
java DigestCalculator SHA1 ../DigitalSignatureExample.java ../README.md ../SymmetricKeyCipherExample.java ../lista_digests.txt

echo
echo not found por hash
java DigestCalculator MD5 ../DigitalSignatureExample.java ../README.md ../SymmetricKeyCipherExample.java ../lista_digests.txt

echo
echo colisao com digest de arquivo
java DigestCalculator MD5 ../DigitalSignatureExample.java ../README.md ../SymmetricKeyCipherExample.java ../lista_digests2.txt

cd ..