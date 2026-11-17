"""Regression checks for accidental publication of private Android inputs."""

import importlib.util
import unittest
from pathlib import Path


spec = importlib.util.spec_from_file_location("public_android", Path(__file__).resolve().parents[1] / "check-public-android.py")
public_android = importlib.util.module_from_spec(spec)
spec.loader.exec_module(public_android)


class PublicationCheckTest(unittest.TestCase):
    def test_local_signing_inputs_are_rejected_even_if_force_added(self):
        for name in ("android/keystore/release.jks", "android/keystore.properties", "android/homemusic.properties", "android/local.properties", "android/.git"):
            with self.subTest(name=name):
                self.assertTrue(public_android.check_file(name, b"example"))

    def test_configuration_examples_and_source_are_allowed(self):
        for name in ("android/keystore.properties.example", "android/homemusic.properties.example", "android/app/src/main/Example.kt", "android/LICENSE"):
            with self.subTest(name=name):
                self.assertEqual([], public_android.check_file(name, b"https://hub.example.com https://203.0.113.10:8445"))

    def test_live_addresses_and_url_credentials_are_rejected(self):
        for url in (b"http://192.168.1.5:8080", b"https://8.8.8.8", b"https://test-user:example-password@hub.example.com"):
            with self.subTest(url=url):
                self.assertTrue(public_android.check_file("android/example.txt", url))

    def test_key_material_and_profile_paths_are_rejected_without_echoing_values(self):
        for value in (b"-----BEGIN PRIVATE KEY-----", b"C:\\Users\\example-user\\project", b"/home/example-user/project"):
            with self.subTest(value=value):
                reasons = public_android.check_file("android/example.txt", value)
                self.assertTrue(reasons)
                self.assertNotIn(value.decode(), " ".join(reasons))


if __name__ == "__main__":
    unittest.main()
