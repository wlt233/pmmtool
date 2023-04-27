# Pmmtool

An Xposed module to change FeliCa PMm of HCE-F for Android NFC.

Tested on:

- Samsung S8 | Android 9
- Redmi K50U | Android 12
- Mi 10 | Android 12
- Redmi K40 | Android 13

## How it works

Use Xposed to inject Dobby into libnfc-nci.so and hook nfa_dm_check_set_config function.

Dobby .so library is bulit and carried in this module.

## Credit

Special Thanks: [@Haocen2004](https://github.com/Haocen2004) [@shacha086](https://github.com/shacha086) [@OLIET2357](https://github.com/OLIET2357)

also see:

[OLIET2357/GeneralFelicaSimulator](https://github.com/OLIET2357/GeneralFelicaSimulator)

[OLIET2357/HCEFUnlocker](https://github.com/OLIET2357/HCEFUnlocker)

[【逆向】论安卓NFC模拟felica时如何修改pmm](https://tqlwsl.moe/index.php/archives/2233/)
