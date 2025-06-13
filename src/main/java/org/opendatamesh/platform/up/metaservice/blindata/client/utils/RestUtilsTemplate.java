package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Template interface for REST operations.
 * <p>
 * This interface provides methods for executing HTTP requests with proper URI parameter handling.
 * URLs should be built using Spring's URI template format:
 * </p>
 * <ul>
 *     <li>Path parameters: Use {paramName} in the URL path
 *         <pre>
 *         Example: "/api/users/{userId}/orders/{orderId}"
 *         Map: {"userId": "123", "orderId": "456"}
 *         Result: "/api/users/123/orders/456"
 *         </pre>
 *     </li>
 *     <li>Query parameters: Use {paramName} in the query string
 *         <pre>
 *         Example: "/api/search?q={query}&page={page}"
 *         Map: {"query": "test", "page": "1"}
 *         Result: "/api/search?q=test&page=1"
 *         </pre>
 *     </li>
 *     <li>Multiple values: Use array notation for multiple values
 *         <pre>
 *         Example: "/api/filter?tags={tags}"
 *         Map: {"tags": ["tag1", "tag2"]}
 *         Result: "/api/filter?tags=tag1&tags=tag2"
 *         </pre>
 *     </li>
 * </ul>
 * <p>
 * The URI parameters are automatically encoded according to RFC 3986.
 * </p>
 */
interface RestUtilsTemplate {
    /**
     * Executes an HTTP request with the specified method and URI parameters.
     *
     * @param url The URL template with placeholders for parameters (e.g., "/api/users/{userId}")
     * @param method The HTTP method to use
     * @param requestEntity The request entity containing headers and body
     * @param responseType The expected response type
     * @param uriVariables Map of URI variables to substitute in the URL template
     *                    Keys should match the placeholders in the URL (e.g., "userId" for "{userId}")
     * @param <T> The response type
     * @return The response body converted to the specified type
     * @throws ClientException if the request fails
     */
    <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException;

    /**
     * Downloads a file from the specified URL.
     *
     * @param url The URL to download from
     * @param httpHeaders The HTTP headers to include in the request
     * @param resource Optional request body
     * @param storeLocation The file where the downloaded content will be stored
     * @return The file where the content was stored
     * @throws ClientException if the download fails
     */
    File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException;
}
