#pragma once
//
// Runtime XOR decoder.
//
// Mirrors the formula in the encodeSecrets Gradle task (app/build.gradle.kts):
//     plain[i] = enc[i] ^ KEY[(i + salt) % KEY_LEN] ^ ((i * 31 + salt) & 0xFF)
//
// The key is read through a volatile pointer so the optimizer cannot
// constant-fold the decode at compile time (which would put the plaintext
// straight back into .rodata / instruction immediates).
//
#include <string>
#include <cstring>
#include "generated/obfuscated_secrets.h"

namespace obf {

__attribute__((noinline))
static std::string decode(const uint8_t *enc, size_t len, uint8_t salt) {
    const volatile uint8_t *key = KEY;
    std::string out(len, '\0');
    for (size_t i = 0; i < len; ++i) {
        const uint8_t k = key[(i + salt) % KEY_LEN];
        const uint8_t mix = static_cast<uint8_t>((i * 31u + salt) & 0xFFu);
        out[i] = static_cast<char>(enc[i] ^ k ^ mix);
    }
    return out;
}

// Overwrite a decoded buffer so plaintext doesn't linger in native heap.
static void wipe(std::string &s) {
    volatile char *p = s.empty() ? nullptr : &s[0];
    for (size_t i = 0; i < s.size(); ++i) p[i] = 0;
    s.clear();
}

}  // namespace obf

// Convenience: OBF_DECODE(BASE_URL) -> std::string
#define OBF_DECODE(NAME) ::obf::decode(::obf::NAME, ::obf::NAME##_LEN, ::obf::NAME##_SALT)
