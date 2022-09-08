/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.rulesengine.language.syntax.fn;

import java.util.HashMap;
import java.util.Optional;
import software.amazon.smithy.rulesengine.language.stdlib.IsValidHostLabel;
import software.amazon.smithy.rulesengine.language.stdlib.ParseArn;
import software.amazon.smithy.rulesengine.language.stdlib.ParseUrl;
import software.amazon.smithy.rulesengine.language.stdlib.PartitionFn;
import software.amazon.smithy.rulesengine.language.stdlib.Substring;
import software.amazon.smithy.rulesengine.language.stdlib.UriEncode;
import software.amazon.smithy.rulesengine.language.util.LazyValue;

public final class FunctionRegistry {
    private static final LazyValue<FunctionRegistry> GLOBAL_REGISTRY =
            LazyValue.<FunctionRegistry>builder().initializer(() -> {
                FunctionRegistry registry = new FunctionRegistry();
                registry.registerFunction(PartitionFn.ID, new PartitionFn());
                registry.registerFunction(IsValidHostLabel.ID, new IsValidHostLabel());
                registry.registerFunction(ParseArn.ID, new ParseArn());
                registry.registerFunction(ParseUrl.ID, new ParseUrl());
                registry.registerFunction(Substring.ID, new Substring());
                registry.registerFunction(UriEncode.ID, new UriEncode());
                return registry;
            }).build();

    private final HashMap<String, FunctionDefinition> registry = new HashMap<>();

    private FunctionRegistry() {
    }

    public void registerFunction(String id, FunctionDefinition definition) {
        registry.put(id, definition);
    }

    public Optional<StandardLibraryFunction> forNode(FnNode node) {
        if (registry.containsKey(node.getId())) {
            return Optional.of(new StandardLibraryFunction(registry.get(node.getId()), node));
        } else {
            return Optional.empty();
        }
    }

    static FunctionRegistry getGlobalRegistry() {
        return GLOBAL_REGISTRY.value();
    }
}
