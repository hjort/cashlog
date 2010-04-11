#!/bin/bash

echo -e "Type this passphrase: mdcsvimporter\n"
java -cp lib/extadmin.jar:lib/moneydance.jar com.moneydance.admin.KeyAdmin signextjar private.key 99 mdcsvimporter target/dist/mdcsvimporter.mxt

echo "Installing into Moneydance modules..."
dst="$HOME/.moneydance/fmodules/mdcsvimporter.mxt"
mv s-mdcsvimporter.mxt $dst

echo "Destin: $dst"
