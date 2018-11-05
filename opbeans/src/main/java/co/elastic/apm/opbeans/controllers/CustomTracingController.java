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

import co.elastic.apm.opbeans.logic.MyBusinessLogic;
import co.elastic.apm.opbeans.logic.MyImportantBusinessLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomTracingController {
	@RequestMapping({ "/ot-example" })
	public ResponseEntity<String> testOT() {
		new MyBusinessLogic().myVeryImportantMethod("Elastic APM Java agent is awesome!!!");
		return new ResponseEntity<>("Succeeded", HttpStatus.OK);
	}

	@RequestMapping({ "/api-example" })
	public ResponseEntity<String> testPublicApi() {
		new MyImportantBusinessLogic().myVeryImportantMethod("Elastic APM Java agent is awesome!!!");
		return new ResponseEntity<>("Succeeded", HttpStatus.OK);
	}
}