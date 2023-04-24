# Pmmtool

An Xposed module to change FeliCa PMm of HCE-F for Android NFC.

**Only tested on Samsung S8**

## How it works

Use Xposed to inject Dobby into libnfc-nci.so and hook nfa_dm_check_set_config function.

Dobby .so libraay is built in this module. The path will be passed to Xposed through a Sdcard file.

## Credit

Special Thanks: OLIET2357

[OLIET2357/GeneralFelicaSimulator](https://github.com/OLIET2357/GeneralFelicaSimulator)

[OLIET2357/HCEFUnlocker](https://github.com/OLIET2357/HCEFUnlocker)
