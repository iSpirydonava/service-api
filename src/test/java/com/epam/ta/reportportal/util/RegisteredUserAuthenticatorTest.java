package com.epam.ta.reportportal.auth.authenticator;

import static com.epam.ta.reportportal.auth.UserRoleHierarchy.ROLE_REGISTERED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epam.ta.reportportal.entity.user.User;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class RegisteredUserAuthenticatorTest {

  private RegisteredUserAuthenticator registeredUserAuthenticator;

  @Mock
  private User mockUser;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    registeredUserAuthenticator = new RegisteredUserAuthenticator();
  }

  @Test
  public void testAuthenticate() {
    // Arrange
    when(mockUser.getLogin()).thenReturn("testUser");
    when(mockUser.getPassword()).thenReturn("testPassword");

    // Act
    Authentication authentication = registeredUserAuthenticator.authenticate(mockUser);

    // Assert
    assertNotNull(authentication);
    assertEquals("testUser", authentication.getName());
    assertEquals("testPassword", authentication.getCredentials());
    assertEquals(1, authentication.getAuthorities().size());
    assertEquals(new SimpleGrantedAuthority(ROLE_REGISTERED), authentication.getAuthorities().iterator().next());
    assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testAuthenticateWithNullUser() {
    // Arrange
    User nullUser = null;

    // Act & Assert
    try {
      registeredUserAuthenticator.authenticate(nullUser);
    } catch (NullPointerException e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testAuthenticateWithEmptyLogin() {
    // Arrange
    when(mockUser.getLogin()).thenReturn("");
    when(mockUser.getPassword()).thenReturn("testPassword");

    // Act
    Authentication authentication = registeredUserAuthenticator.authenticate(mockUser);

    // Assert
    assertNotNull(authentication);
    assertEquals("", authentication.getName());
    assertEquals("testPassword", authentication.getCredentials());
    assertEquals(1, authentication.getAuthorities().size());
    assertEquals(new SimpleGrantedAuthority(ROLE_REGISTERED), authentication.getAuthorities().iterator().next());
    assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testAuthenticateWithEmptyPassword() {
    // Arrange
    when(mockUser.getLogin()).thenReturn("testUser");
    when(mockUser.getPassword()).thenReturn("");

    // Act
    Authentication authentication = registeredUserAuthenticator.authenticate(mockUser);

    // Assert
    assertNotNull(authentication);
    assertEquals("testUser", authentication.getName());
    assertEquals("", authentication.getCredentials());
    assertEquals(1, authentication.getAuthorities().size());
    assertEquals(new SimpleGrantedAuthority(ROLE_REGISTERED), authentication.getAuthorities().iterator().next());
    assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
  }
}
