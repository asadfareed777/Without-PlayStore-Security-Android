# Native methods are bound by NAME via RegisterNatives (names are XOR-encoded in C++).
# Keep the class and its native method names so the binding still matches after R8.
-keep class com.demo.securekeys.NativeKeys {
    native <methods>;
}
