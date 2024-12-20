/*
 * Copyright 2021 Appmattus Limited
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

package com.appmattus.kotlinfixture.config

import com.appmattus.kotlinfixture.decorator.Decorator
import com.appmattus.kotlinfixture.decorator.exception.ExceptionDecorator
import com.appmattus.kotlinfixture.decorator.filter.Filter
import com.appmattus.kotlinfixture.decorator.filter.FilterDecorator
import com.appmattus.kotlinfixture.decorator.logging.LoggingDecorator
import com.appmattus.kotlinfixture.decorator.recursion.RecursionDecorator
import com.appmattus.kotlinfixture.resolver.AbstractClassResolver
import com.appmattus.kotlinfixture.resolver.AndroidUriResolver
import com.appmattus.kotlinfixture.resolver.ArrayKTypeResolver
import com.appmattus.kotlinfixture.resolver.AtomicKTypeResolver
import com.appmattus.kotlinfixture.resolver.BigDecimalResolver
import com.appmattus.kotlinfixture.resolver.BigIntegerResolver
import com.appmattus.kotlinfixture.resolver.CalendarResolver
import com.appmattus.kotlinfixture.resolver.CharResolver
import com.appmattus.kotlinfixture.resolver.ClassResolver
import com.appmattus.kotlinfixture.resolver.CurrencyResolver
import com.appmattus.kotlinfixture.resolver.DateResolver
import com.appmattus.kotlinfixture.resolver.EnumMapResolver
import com.appmattus.kotlinfixture.resolver.EnumResolver
import com.appmattus.kotlinfixture.resolver.EnumSetResolver
import com.appmattus.kotlinfixture.resolver.FactoryMethodResolver
import com.appmattus.kotlinfixture.resolver.FactoryResolver
import com.appmattus.kotlinfixture.resolver.FakeResolver
import com.appmattus.kotlinfixture.resolver.FileResolver
import com.appmattus.kotlinfixture.resolver.FormatResolver
import com.appmattus.kotlinfixture.resolver.HashtableKTypeResolver
import com.appmattus.kotlinfixture.resolver.IterableKTypeResolver
import com.appmattus.kotlinfixture.resolver.JodaTimeResolver
import com.appmattus.kotlinfixture.resolver.KFunctionResolver
import com.appmattus.kotlinfixture.resolver.KNamedPropertyResolver
import com.appmattus.kotlinfixture.resolver.KTormResolver
import com.appmattus.kotlinfixture.resolver.KTypeResolver
import com.appmattus.kotlinfixture.resolver.LocaleResolver
import com.appmattus.kotlinfixture.resolver.MapKTypeResolver
import com.appmattus.kotlinfixture.resolver.ObjectResolver
import com.appmattus.kotlinfixture.resolver.PrimitiveArrayResolver
import com.appmattus.kotlinfixture.resolver.PrimitiveResolver
import com.appmattus.kotlinfixture.resolver.Resolver
import com.appmattus.kotlinfixture.resolver.SealedClassResolver
import com.appmattus.kotlinfixture.resolver.StringResolver
import com.appmattus.kotlinfixture.resolver.SubTypeResolver
import com.appmattus.kotlinfixture.resolver.ThreeTenResolver
import com.appmattus.kotlinfixture.resolver.TimeResolver
import com.appmattus.kotlinfixture.resolver.TupleKTypeResolver
import com.appmattus.kotlinfixture.resolver.UriResolver
import com.appmattus.kotlinfixture.resolver.UrlResolver
import com.appmattus.kotlinfixture.resolver.UuidResolver
import com.appmattus.kotlinfixture.toUnmodifiableList
import com.appmattus.kotlinfixture.toUnmodifiableMap
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * The [Configuration] for generating the current fixture. This is a combination of all previous configurations.
 * @property repeatCount The length used for lists and maps.
 * @property propertiesRepeatCount Overrides the length used for lists and maps on constructor parameters and mutable properties when generating instances of generic classes.
 * @property properties Overrides for constructor parameters and mutable properties when generating instances of generic classes.
 * @property factories Given instances for a particular class using a factory method.
 * @property subTypes Superclass to subclass mapping for subtypes.
 * @property random Random to use for generating random values. This may be a seeded random.
 * @property decorators Each [Decorator] wraps the resolver chain.
 * @property resolvers The resolver chain, each [Resolver] is called in order until one handles the input object.
 * @property strategies Strategy settings for altering the behaviour of [resolvers] and [decorators].
 * @property filters Sequence filters for generated values.
 */
@ConsistentCopyVisibility
data class Configuration internal constructor(
    val repeatCount: () -> Int = defaultRepeatCount,
    val propertiesRepeatCount: Map<KClass<*>, Map<String, () -> Int>> =
        emptyMap<KClass<*>, Map<String, () -> Int>>().toUnmodifiableMap(),
    val properties: Map<KClass<*>, Map<String, GeneratorFun>> =
        emptyMap<KClass<*>, Map<String, GeneratorFun>>().toUnmodifiableMap(),
    val factories: Map<KType, GeneratorFun> =
        emptyMap<KType, GeneratorFun>().toUnmodifiableMap(),
    val subTypes: Map<KClass<*>, KClass<*>> = emptyMap<KClass<*>, KClass<*>>().toUnmodifiableMap(),
    val random: Random = defaultRandom,
    val decorators: List<Decorator> = defaultDecorators.toUnmodifiableList(),
    val resolvers: List<Resolver> = defaultResolvers.toUnmodifiableList(),
    val strategies: Map<KClass<*>, Any> = emptyMap<KClass<*>, Any>().toUnmodifiableMap(),
    internal val filters: Map<KType, Filter> = emptyMap<KType, Filter>().toUnmodifiableMap()
) {

    private companion object {
        private val defaultRepeatCount: () -> Int = { 5 }

        private val defaultRandom = Random

        private val defaultDecorators = listOf(
            FilterDecorator(),
            ExceptionDecorator(),
            RecursionDecorator(),
            LoggingDecorator()
        )

        private val defaultResolvers = listOf(
            FactoryResolver(),
            SubTypeResolver(),

            CharResolver(),
            StringResolver(),
            PrimitiveResolver(),
            UrlResolver(),
            UriResolver(),
            AndroidUriResolver(),
            BigDecimalResolver(),
            BigIntegerResolver(),
            UuidResolver(),
            EnumResolver(),
            CalendarResolver(),
            DateResolver(),
            TimeResolver(),
            JodaTimeResolver(),
            ThreeTenResolver(),
            FileResolver(),
            FormatResolver(),
            CurrencyResolver(),
            LocaleResolver(),
            KTormResolver(),

            ObjectResolver(),
            SealedClassResolver(),

            AtomicKTypeResolver(),
            TupleKTypeResolver(),

            ArrayKTypeResolver(),
            PrimitiveArrayResolver(),
            HashtableKTypeResolver(),
            IterableKTypeResolver(),
            EnumSetResolver(),
            EnumMapResolver(),
            MapKTypeResolver(),

            KTypeResolver(),
            FakeResolver(),
            KNamedPropertyResolver(),
            KFunctionResolver(),

            AbstractClassResolver(),

            ClassResolver(),
            FactoryMethodResolver()
        )
    }
}
