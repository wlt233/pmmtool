# Pmmtool

An Xposed module to change FeliCa PMm of HCE-F for Android NFC.

**Only tested on Samsung S8 & Android 9 with EDXposed**

Not works with LSPosed

## How it works

Use Xposed to inject Dobby into libnfc-nci.so and hook nfa_dm_check_set_config function.

Dobby .so library is bulit and carried in this module.

## Credit

Special Thanks: @Haocen2004 @OLIET2357

also see:

[OLIET2357/GeneralFelicaSimulator](https://github.com/OLIET2357/GeneralFelicaSimulator)

[OLIET2357/HCEFUnlocker](https://github.com/OLIET2357/HCEFUnlocker)

[【逆向】论安卓NFC模拟felica时如何修改pmm](https://tqlwsl.moe/index.php/archives/2233/)
