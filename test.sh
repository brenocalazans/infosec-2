#!/bin/bash
javac src/DigestCalculator.java
java src/DigestCalculator SHA1 DigitalSignatureExample.java SymmetricKeyCipherExample.java DigitalSignatureExample\(copy\).java lista_digests.txt
