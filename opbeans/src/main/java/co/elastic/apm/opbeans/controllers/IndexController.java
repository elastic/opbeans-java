/*-
 * #%L
 * Opbeans Java Demo Application
 * %%
 * Copyright (C) 2018 the original author or authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package co.elastic.apm.opbeans.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    Environment env;

    @RequestMapping({ "/{path:[^\\\\.]*}", "/dashboard", "/products/*", "/orders/*", "/customers/*" })
    public String redirect() {
        return "forward:/";
    }

    @RequestMapping({ "/is-it-coffee-time" })
    public String error(@RequestParam(value = "cause", required = false) String causeMsg) {
        String msg = "Demo exception";
        if (causeMsg != null) {
            // allows to test for chained exceptions
            throw new RuntimeException(msg, new RuntimeException(causeMsg));
        } else {
            // regular non-chained exception
            throw new RuntimeException(msg);
        }
    }

    @RequestMapping(value = { "/rum-config.js" })
    @ResponseBody
    public String rumConfig(HttpServletResponse response) {
        response.setContentType("text/javascript");
        return String.format("window.elasticApmJsBaseServerUrl = '%s'",
                env.getProperty("ELASTIC_APM_JS_SERVER_URL", "http://localhost:" + env.getProperty("local.server.port", "80")));
    }
}
