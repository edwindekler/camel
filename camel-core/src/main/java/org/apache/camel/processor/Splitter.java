/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Expression;
import static org.apache.camel.util.ObjectHelper.*;

import java.util.Iterator;

/**
 * Implements a dynamic <a href="http://activemq.apache.org/camel/splitter.html">Splitter</a> pattern
 * where an expression is evaluated to iterate through each of the parts of a message and then each part is then send to some endpoint.
 *
 * @version $Revision$
 */
public class Splitter<E extends Exchange> implements Processor<E> {
    private final Processor<E> destination;
    private final Expression<E> expression;

    public Splitter(Processor<E> destination, Expression<E> expression) {
        this.destination = destination;
        this.expression = expression;
        notNull(destination, "destination");
        notNull(expression, "expression");
    }

    @Override
    public String toString() {
        return "Splitter[on: " + expression + " to: " + destination + "]";
    }

    public void onExchange(E exchange) {
        Object value = expression.evaluate(exchange);
        Iterator iter = createIterator(value);
        while (iter.hasNext()) {
            Object part = iter.next();
            E newExchange = (E) exchange.copy();
            newExchange.getIn().setBody(part);
            destination.onExchange(newExchange);
        }
    }
}
