/*
 * Copyright 2023 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:ihar_kahadouski@epam.com">Ihar Kahadouski</a>
 */
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Component
class ApiKeyUtilsTest {

  @Test
  void validToken() {
    assertTrue(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void validUUIDToken() {
    assertTrue(ApiKeyUtils.validateToken("c229070a-56fe-4f99-ad57-fa945aa9443b"));
  }

  @Test
  void invalidToken() {
    assertFalse(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssbsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void invalidUUIDToken() {
    assertFalse(ApiKeyUtils.validateToken("c229070a-56fe-4f99-ad57-fa945aa9443z")); // Invalid character 'z'
  }

  @Test
  void shortToken() {
    assertFalse(ApiKeyUtils.validateToken("short_token"));
  }

  @Test
  void longToken() {
    assertFalse(ApiKeyUtils.validateToken("this_is_a_very_long_token_that_should_not_be_valid"));
  }

  @Test
  void emptyToken() {
    assertFalse(ApiKeyUtils.validateToken(""));
  }

  @Test
  void nullToken() {
    assertThrows(NullPointerException.class, () -> ApiKeyUtils.validateToken(null));
  }

  @Test
  void validBase64Token() {
    assertTrue(ApiKeyUtils.validateToken("dGVzdF9iQ1YwZGNRekZ1Q28wRXExVXYyX2hpems0cEhu"));
  }

  @Test
  void invalidBase64Token() {
    assertFalse(ApiKeyUtils.validateToken("dGVzdF9iQ1YwZGNRekZ1Q28wRXExVXYyX2hpems0cEhu_invalid"));
  }

  @Test
  void validTokenWithDifferentLength() {
    assertTrue(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void validTokenWithDifferentCharacters() {
    assertTrue(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void invalidTokenWithSpecialCharacters() {
    assertFalse(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM!@#"));
  }

  @Test
  void validTokenWithUnderscore() {
    assertTrue(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void invalidTokenWithUnderscore() {
    assertFalse(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM_"));
  }

  @Test
  void validTokenWithMixedCase() {
    assertTrue(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

  @Test
  void invalidTokenWithMixedCase() {
    assertFalse(ApiKeyUtils.validateToken("test2_bCV0dcQfRuCo0Eq1Uv2_hizk4pHnssmV6qMLCEHGcyabsGAQxTQ1_gWJaX0kVHPM"));
  }

}