/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.server.resources;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Test;

import de.dlr.sc.virsat.server.auth.AuthFilter;
import de.dlr.sc.virsat.server.auth.BasicServerUserHandler;
import de.dlr.sc.virsat.server.auth.ServerConfiguration;
import de.dlr.sc.virsat.server.test.AGitAndJettyServerTest;

public class AuthTestResourceTest extends AGitAndJettyServerTest {
	
	@Test
	public void testAuthentication() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		URI uri = UriBuilder.fromUri("http://localhost:8000/").build();
		WebTarget target = client.target(uri);
		
		ServerConfiguration.getInstance().setServerUserHandler(BasicServerUserHandler.class.getName());
		
		String serverResponse = target.
			path("/rest").
			path("/auth").
			path("/denied").
			request().
			get(Response.class)
			.toString();
		
		String expectedResponse = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/denied, status=403, reason=Forbidden}}";
		
		assertEquals("Server response is correct", expectedResponse, serverResponse);
		
		String serverResponse2 = target.
			path("/rest").
			path("/auth").
			path("/permitted").
			request().
			get(Response.class)
			.toString();
		
		String serverString = target.
			path("/rest").
			path("/auth").
			path("/permitted").
			request().
			get(String.class);
		
		String expectedResponse2 = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/permitted, status=200, reason=OK}}";
		
		assertEquals("Server response is correct", expectedResponse2, serverResponse2);
		assertEquals("Server String payload is correct", AuthTestResource.PERMITTED, serverString);
	}
	
	@Test
	public void testAuthorization() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		URI uri = UriBuilder.fromUri("http://localhost:8000/").build();
		WebTarget target = client.target(uri);
		
		ServerConfiguration.getInstance().setServerUserHandler(BasicServerUserHandler.class.getName());
		
		String serverResponse = target.
			path("/rest").
			path("/auth").
			path("/all_users").
			request().
			get(Response.class)
			.toString();
		
		String expectedResponse = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/all_users, status=401, reason=Unauthorized}}";
		assertEquals("Unauthorized response because of missing header", expectedResponse, serverResponse);
	
		String serverResponse2 = target.
				path("/rest").
				path("/auth").
				path("/all_users").
				request().
				header(HttpHeaders.AUTHORIZATION, "").
				get(Response.class)
				.toString();
		
		assertEquals("Unauthorized response because of empty header", expectedResponse, serverResponse2);
		
		String serverResponse3 = target.
				path("/rest").
				path("/auth").
				path("/all_users").
				request().
				header(HttpHeaders.AUTHORIZATION, "username:password").
				get(Response.class)
				.toString();
		
		assertEquals("Unauthorized response because of wrong header", expectedResponse, serverResponse3);
		
		String encoded = AuthFilter.BASIC_SCHEME + " " + Base64.getEncoder().encodeToString("unknown:password".getBytes());
		String serverResponse4 = target.
				path("/rest").
				path("/auth").
				path("/all_users").
				request().
				header(HttpHeaders.AUTHORIZATION, encoded).
				get(Response.class)
				.toString();
		
		assertEquals("Unauthorized response because of unknown user", expectedResponse, serverResponse4);
		
		String encoded2 = AuthFilter.BASIC_SCHEME + " " + Base64.getEncoder().encodeToString("user:password".getBytes());
		String serverResponse5 = target.
				path("/rest").
				path("/auth").
				path("/all_users").
				request().
				header(HttpHeaders.AUTHORIZATION, encoded2).
				get(Response.class)
				.toString();
		
		String expectedResponse5 = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/all_users, status=200, reason=OK}}";
		assertEquals("Correct server response: user correctly authenticated and authorized", expectedResponse5, serverResponse5);
	}

	@Test
	public void testServerRoles() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		
		URI uri = UriBuilder.fromUri("http://localhost:8000/").build();
		WebTarget target = client.target(uri);
		
		ServerConfiguration.getInstance().setServerUserHandler(BasicServerUserHandler.class.getName());
		
		String encodedUser = AuthFilter.BASIC_SCHEME + " " + Base64.getEncoder().encodeToString("user:password".getBytes());
		String serverResponse = target.
				path("/rest").
				path("/auth").
				path("/user_only").
				request().
				header(HttpHeaders.AUTHORIZATION, encodedUser).
				get(Response.class)
				.toString();
			
		String expectedResponse = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/user_only, status=200, reason=OK}}";
		assertEquals("User can access user only ressource", expectedResponse, serverResponse);
		
		String serverResponse2 = target.
				path("/rest").
				path("/auth").
				path("/admin_only").
				request().
				header(HttpHeaders.AUTHORIZATION, encodedUser).
				get(Response.class)
				.toString();
			
		String expectedResponse2 = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/admin_only, status=401, reason=Unauthorized}}";
		assertEquals("User can't access admin only ressource", expectedResponse2, serverResponse2);
		
		String encodedAdmin = AuthFilter.BASIC_SCHEME + " " + Base64.getEncoder().encodeToString("admin:password".getBytes());
		String serverResponse3 = target.
				path("/rest").
				path("/auth").
				path("/admin_only").
				request().
				header(HttpHeaders.AUTHORIZATION, encodedAdmin).
				get(Response.class)
				.toString();
			
		String expectedResponse3 = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/admin_only, status=200, reason=OK}}";
		assertEquals("Admin can access admin only ressource", expectedResponse3, serverResponse3);
		
		String serverResponse4 = target.
				path("/rest").
				path("/auth").
				path("/user_only").
				request().
				header(HttpHeaders.AUTHORIZATION, encodedAdmin).
				get(Response.class)
				.toString();
			
		String expectedResponse4 = "InboundJaxrsResponse{context=ClientResponse{method=GET, uri=http://localhost:8000/rest/auth/user_only, status=401, reason=Unauthorized}}";
		assertEquals("Admin can't access user only ressource", expectedResponse4, serverResponse4);
	}
}