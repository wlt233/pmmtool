
#include "main.h"
#include <dobby.h>

char buff[30];
char pmm_str[30];
//char target_pmm[8] = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
char target_pmm[8] = {0x00, 0xf1, 0x00, 0x00, 0x00, 0x01, 0x43, 0x00};

void *new_func(u_int8_t a1, u_int8_t *a2, int a3) {
    __android_log_print(6, "pmmtool", "hook _Z23nfa_dm_check_set_confighPhb arg0->%x arg1->%x", a1, a2);
    if (a1 == 0x1d) {
        for (int i = 0x0; i < 0x10; ++i)
            sprintf(buff + i * 3, "%02x ", *(char *)(a2 + i));
        __android_log_print(6, "pmmtool", "[%x]: %s", a2, buff);
        for (int i = 0x0; i < 0x10; ++i)
            sprintf(buff + i * 3, "%02x ", *(char *)(a2 + 0x10 + i));
        __android_log_print(6, "pmmtool", "[%x]: %s", a2 + 0x10, buff);
        for (int i = 0x0; i < 0x20; ++i) {
            if (*(char *)(a2 + i) == 0x51 && *(char *)(a2 + i + 1) == 0x08) {
                for (int j = 0; j < 8; ++j)
                    sprintf(pmm_str + j * 3, "%02x ", *(char *)(a2 + i + 2 + j));
                __android_log_print(6, "pmmtool", "old PMm: %s", pmm_str);
                for (int j = 0; j < 8; ++j)
                    *(char *)(a2 + i + 2 + j) = target_pmm[j];
                for (int j = 0; j < 8; ++j)
                    sprintf(pmm_str + j * 3, "%02x ", *(char *)(a2 + i + 2 + j));
                __android_log_print(6, "pmmtool", "new PMm: %s", pmm_str);
            }
        }
    }
    //__android_log_print(6, "pmmtool", "load old func");
    void *result = old_func(a1, a2, a3);
    //__android_log_print(6, "pmmtool", "hook result -> %x", result);
    return result;
}

jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(6, "pmmtool", "Inside JNI_OnLoad");
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_OK) {
        //void *func_addr = DobbySymbolResolver("libnfc-nci.so", "_Z23nfa_dm_check_set_confighPhb");
        void *func_addr = DobbySymbolResolver(NULL, "_Z23nfa_dm_check_set_confighPhb");
        __android_log_print(6, "pmmtool", "_Z23nfa_dm_check_set_confighPhb addr->%x", func_addr);
        DobbyHook(func_addr, (void *) new_func, (void **) &old_func);
        __android_log_print(6, "pmmtool", "Dobby hooked");
        return JNI_VERSION_1_6;
    }
    return 0;
}
