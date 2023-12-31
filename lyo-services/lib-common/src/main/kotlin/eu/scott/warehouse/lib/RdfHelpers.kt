/*
 * Copyright (c) 2019 Ericsson Research and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.scott.warehouse.lib

import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.RDFFormat
import org.apache.jena.riot.RDFParser
import org.apache.jena.util.ResourceUtils
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException
import org.eclipse.lyo.oslc4j.core.model.IResource
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import java.net.URI
import java.util.UUID
import javax.xml.datatype.DatatypeConfigurationException


object RdfHelpers {

    val log: Logger = LoggerFactory.getLogger(RdfHelpers::class.java)

    @JvmStatic
    fun randomUuidUrn(): URI = URI.create("urn:uuid:" + UUID.randomUUID().toString())

    @JvmStatic
    @JvmOverloads
    fun modelToString(model: Model, rdfFormat: RDFFormat = RDFFormat.TURTLE_PRETTY): String {
        val writer = StringWriter(1000)
        RDFDataMgr.write(writer, model, rdfFormat)
        return writer.toString()
    }

    @JvmStatic
    fun modelFromResources(vararg objects: IResource): Model {
        try {
            return JenaModelHelper.createJenaModel(objects)
        } catch (e: DatatypeConfigurationException) {
            log.error("Shape definition error, check your annotations", e)
            throw IllegalStateException(e)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException(e)
        } catch (e: InvocationTargetException) {
            throw IllegalStateException(e)
        } catch (e: OslcCoreApplicationException) {
            throw IllegalStateException(e)
        }
    }



    @JvmStatic
    fun modelFromIndentedString(str: String, l: Lang = Lang.TURTLE): Model {
        val model = ModelFactory.createDefaultModel()
        RDFParser.fromString(str.trimIndent()).lang(l).parse(model.graph)
        return model
    }

    /**
     * Skolemizes the model in-situ.
     */
    @JvmStatic
    fun skolemize(m: Model) {
        val resIterator = m.listSubjects()
        while (resIterator.hasNext()) {
            val resource = resIterator.nextResource()
            if (resource != null && resource.isAnon) {
                val skolemURI = "urn:skolem:" + resource.id.blankNodeId.labelString
                ResourceUtils.renameResource(resource, skolemURI)
            }
        }
    }

    fun modelFromTurtleResource(clazz: Class<*>, path: String): Model {
        return modelFromResourceFile(clazz, path, Lang.TURTLE)
    }

    private fun modelFromResourceFile(clazz: Class<*>, path: String, lang: Lang): Model {
        val inputStream = clazz.classLoader.getResourceAsStream(path)
        if(inputStream == null) {
            val message = "'$path' cannot be loaded from JAR resources, file not found."
            log.error(message)
            throw IllegalArgumentException(message)
        }
        val model = ModelFactory.createDefaultModel()
        RDFDataMgr.read(model, inputStream, lang)
        return model
    }


}
