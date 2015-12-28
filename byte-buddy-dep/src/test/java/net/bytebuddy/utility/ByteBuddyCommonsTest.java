package net.bytebuddy.utility;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.asm.Opcodes;

import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static net.bytebuddy.utility.ByteBuddyCommons.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ByteBuddyCommonsTest {

    private static final String FOO = "foo", BAR = "bar", QUX = "qux", BAZ = "baz", FOOBAR = "foo.bar", PUBLIC = "public";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription first, second;

    @Before
    public void setUp() throws Exception {
        when(first.getInternalName()).thenReturn(FOO);
        when(first.isAssignableTo(Throwable.class)).thenReturn(true);
        when(second.getInternalName()).thenReturn(BAR);
        when(second.isAssignableTo(Throwable.class)).thenReturn(false);
    }

    @Test
    public void testNonNull() throws Exception {
        Object object = new Object();
        assertThat(nonNull(object), sameInstance(object));
    }

    @Test(expected = NullPointerException.class)
    public void testNonNullThrowsException() throws Exception {
        nonNull(null);
    }

    @Test
    public void testNonNullArray() throws Exception {
        Object[] object = new Object[]{new Object()};
        assertThat(nonNull(object), sameInstance(object));
    }

    @Test(expected = NullPointerException.class)
    public void testNonNullArrayThrowsException() throws Exception {
        nonNull(new Object[1]);
    }

    @Test
    public void testIsAnnotation() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Retention.class);
        assertThat(isAnnotation(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsAnnotationThrowsException() throws Exception {
        isAnnotation(TypeDescription.OBJECT);
    }

    @Test
    public void testIsThrowable() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Throwable.class);
        assertThat(isThrowable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsThrowableThrowsExceptionForWildcard() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        isThrowable(typeDescription);
    }

    @Test
    public void testIsThrowableForExceptionVariable() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        when(typeDescription.asErasure()).thenReturn(new TypeDescription.ForLoadedType(Throwable.class));
        assertThat(isThrowable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsThrowableThrowsExceptionForExceptionVariableOfNonThrowableType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        isThrowable(typeDescription);
    }

    @Test
    public void testIsThrowableCollection() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Throwable.class);
        List<TypeDescription> typeDescriptions = Collections.singletonList(typeDescription);
        assertThat(isThrowable(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsThrowableCollectionThrowsException() throws Exception {
        TypeDescription typeDescription = TypeDescription.OBJECT;
        List<TypeDescription> typeDescriptions = Collections.singletonList(typeDescription);
        isThrowable(typeDescriptions);
    }

    @Test
    public void testIsDefineable() throws Exception {
        TypeDescription typeDescription = TypeDescription.OBJECT;
        assertThat(isDefineable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveTypeIsDefineableThrowsException() throws Exception {
        isDefineable(new TypeDescription.ForLoadedType(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayTypeIsDefineableThrowsException() throws Exception {
        isDefineable(new TypeDescription.ForLoadedType(Object[].class));
    }

    @Test
    public void testIsExtendable() throws Exception {
        TypeDescription typeDescription = TypeDescription.OBJECT;
        assertThat(isExtendable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveTypeIsExtendableThrowsException() throws Exception {
        isExtendable(new TypeDescription.ForLoadedType(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayTypeIsExtendableThrowsException() throws Exception {
        isExtendable(new TypeDescription.ForLoadedType(Object[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalTypeIsExtendableThrowsException() throws Exception {
        isExtendable(TypeDescription.STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeVaribaleTypeIsExtendableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        isExtendable(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWildcardTypeIsExtendableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        isExtendable(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericArrayTypeIsExtendableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.GENERIC_ARRAY);
        isExtendable(typeDescription);
    }

    @Test
    public void testParameterizedTypeIsExtendable() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        assertThat(isExtendable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParameterizedTypeWithIllegalErasureIsExtendableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.STRING);
        isExtendable(typeDescription);
    }

    @Test
    public void testIsImplementable() throws Exception {
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(Runnable.class);
        assertThat(isImplementable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassIsImplementableThrowsException() throws Exception {
        isImplementable(TypeDescription.OBJECT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPrimitiveTypeIsImplementableThrowsException() throws Exception {
        isImplementable(new TypeDescription.ForLoadedType(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrayTypeIsImplementableThrowsException() throws Exception {
        isImplementable(new TypeDescription.ForLoadedType(Object[].class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeVaribaleTypeIsImplementableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        isImplementable(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWildcardTypeIsImplementableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        isImplementable(typeDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericArrayTypeIsImplementableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.GENERIC_ARRAY);
        isImplementable(typeDescription);
    }

    @Test
    public void testParameterizedTypeIsImplementable() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        when(typeDescription.asErasure()).thenReturn(new TypeDescription.ForLoadedType(Runnable.class));
        assertThat(isImplementable(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParameterizedTypeWithIllegalErasureIsImplementableThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        isImplementable(typeDescription);
    }

    @Test
    public void testCollectionIsImplementable() throws Exception {
        Collection<TypeDescription> typeDescriptions = Collections.<TypeDescription>singleton(new TypeDescription.ForLoadedType(Runnable.class));
        assertThat(isImplementable(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionIsImplementableThrowsException() throws Exception {
        isImplementable(Collections.<TypeDescription>singleton(TypeDescription.OBJECT));
    }

    @Test
    public void testIsActualTypeOrVoidForRawType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.NON_GENERIC);
        assertThat(isActualTypeOrVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testIsActualTypeOrVoidForGenericArray() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.GENERIC_ARRAY);
        assertThat(isActualTypeOrVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testIsActualTypeOrVoidForTypeVariable() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        assertThat(isActualTypeOrVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testIsActualTypeOrVoidForParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        assertThat(isActualTypeOrVoid(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsActualTypeOrVoidForWildcardThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        isActualTypeOrVoid(typeDescription);
    }

    @Test
    public void testCollectionIsActualTypeOrVoid() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.NON_GENERIC);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.VOID);
        Collection<TypeDescription.Generic> typeDescriptions = Collections.singleton(typeDescription);
        assertThat(isActualTypeOrVoid(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionIsActualTypeOrVoidThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        isActualTypeOrVoid(Collections.singleton(typeDescription));
    }

    @Test
    public void testIsActualTypeForRawType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.NON_GENERIC);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        assertThat(isActualType(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsActualTypeForRawVoidThrowsException() throws Exception {
        isActualType(TypeDescription.VOID);
    }

    @Test
    public void testIsActualTypeForGenericArray() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.GENERIC_ARRAY);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        assertThat(isActualType(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testIsActualTypeForTypeVariable() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.VARIABLE);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        assertThat(isActualType(typeDescription), sameInstance(typeDescription));
    }

    @Test
    public void testIsActualTypeForParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.PARAMETERIZED);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        assertThat(isActualType(typeDescription), sameInstance(typeDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsActualTypeForWildcardThrowsException() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.WILDCARD);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        isActualType(typeDescription);
    }

    @Test
    public void testCollectionIsActualType() throws Exception {
        TypeDescription.Generic typeDescription = mock(TypeDescription.Generic.class);
        when(typeDescription.getSort()).thenReturn(TypeDefinition.Sort.NON_GENERIC);
        when(typeDescription.asErasure()).thenReturn(TypeDescription.OBJECT);
        Collection<TypeDescription.Generic> typeDescriptions = Collections.singleton(typeDescription);
        assertThat(isActualType(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionIsActualTypeThrowsException() throws Exception {
        isActualType(Collections.singleton(TypeDescription.VOID));
    }

    @Test
    public void testJoinListAndElement() throws Exception {
        assertThat(join(Arrays.asList(FOO, BAR), QUX), is(Arrays.asList(FOO, BAR, QUX)));
    }

    @Test
    public void testJoinElementAndList() throws Exception {
        assertThat(join(FOO, Arrays.asList(BAR, QUX)), is(Arrays.asList(FOO, BAR, QUX)));
    }

    @Test
    public void testJoinListAndList() throws Exception {
        assertThat(join(Arrays.asList(FOO, BAR), Arrays.asList(QUX, BAZ)), is(Arrays.asList(FOO, BAR, QUX, BAZ)));
    }

    @Test
    public void testFilterUniqueNoDuplicates() throws Exception {
        assertThat(filterUnique(Arrays.asList(FOO, BAR), Arrays.asList(QUX, BAZ)), is(Arrays.asList(FOO, BAR, QUX, BAZ)));
    }

    @Test
    public void testFilterUniqueDuplicates() throws Exception {
        assertThat(filterUnique(Arrays.asList(FOO, BAR), Arrays.asList(FOO, BAZ)), is(Arrays.asList(FOO, BAR, BAZ)));
    }

    @Test
    public void testJoinUnique() throws Exception {
        assertThat(joinUnique(Arrays.asList(FOO, BAR), QUX), is(Arrays.asList(FOO, BAR, QUX)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinUniqueDuplicate() throws Exception {
        joinUnique(Arrays.asList(FOO, BAR), FOO);
    }

    @Test
    public void testUniqueRaw() throws Exception {
        TypeDescription first = mock(TypeDescription.class), second = mock(TypeDescription.class);
        when(first.asErasure()).thenReturn(first);
        when(second.asErasure()).thenReturn(second);
        Collection<TypeDescription> typeDescriptions = Arrays.asList(first, second);
        assertThat(uniqueRaw(typeDescriptions), sameInstance(typeDescriptions));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUniqueRawThrowsException() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(typeDescription.asErasure()).thenReturn(typeDescription);
        Collection<TypeDescription> typeDescriptions = Arrays.asList(typeDescription, typeDescription);
        uniqueRaw(typeDescriptions);
    }

    @Test
    public void testJoinUniqueRaw() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(typeDescription.asErasure()).thenReturn(typeDescription);
        assertThat(joinUniqueRaw(Collections.singleton(typeDescription), Collections.singleton(typeDescription)),
                is(Collections.singletonList(typeDescription)));
    }

    @Test
    public void testJoinUniqueRawWithDuplicate() throws Exception {
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(typeDescription.asErasure()).thenReturn(typeDescription);
        assertThat(joinUniqueRaw(Collections.singleton(typeDescription), Collections.singleton(typeDescription)),
                is(Collections.singletonList(typeDescription)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinUniqueRawWithConflictingDuplicate() throws Exception {
        TypeDescription.Generic first = mock(TypeDescription.Generic.class), second = mock(TypeDescription.Generic.class);
        TypeDescription typeDescription = mock(TypeDescription.class);
        when(first.asErasure()).thenReturn(typeDescription);
        when(second.asErasure()).thenReturn(typeDescription);
        joinUniqueRaw(Collections.singletonList(first), Collections.singleton(second));
    }

    @Test
    public void testIsValidIdentifier() throws Exception {
        assertThat(isValidIdentifier(FOO), is(FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierInvalidTokenStartThrowsException() throws Exception {
        isValidIdentifier(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierInvalidTokenMiddleThrowsException() throws Exception {
        isValidIdentifier(FOO + ">");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIdentifierAsKeywordThrowsException() throws Exception {
        isValidIdentifier(PUBLIC);
    }

    @Test
    public void testIsValidTypeName() throws Exception {
        assertThat(isValidTypeName(FOOBAR), is(FOOBAR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidTypeNameThrowsException() throws Exception {
        assertThat(isValidTypeName("." + FOO), is(FOOBAR));
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        List<String> list = Collections.singletonList(FOO);
        assertThat(isNotEmpty(list, FOO), sameInstance(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNotEmptyThrowsException() throws Exception {
        isNotEmpty(Collections.emptyList(), FOO);
    }

    @Test
    public void testIsEmpty() throws Exception {
        List<String> list = Collections.emptyList();
        assertThat(isEmpty(list, FOO), sameInstance(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsEmptyThrowsException() throws Exception {
        isEmpty(Collections.singletonList(BAR), FOO);
    }

    @Test
    public void testResolveModifierContributors() throws Exception {
        assertThat(resolveModifierContributors(ByteBuddyCommons.FIELD_MODIFIER_MASK,
                FieldManifestation.FINAL,
                Ownership.STATIC,
                Visibility.PRIVATE), is(Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveModifierContributorsDuplication() throws Exception {
        resolveModifierContributors(Integer.MAX_VALUE, Ownership.STATIC, Ownership.MEMBER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveModifierContributorsMask() throws Exception {
        resolveModifierContributors(ModifierContributor.EMPTY_MASK, Ownership.STATIC);
    }

    @Test
    public void testUniqueForUniqueTypes() throws Exception {
        assertThat(unique(Arrays.asList(first, second)), is(Arrays.asList(first, second)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUniqueForNonUniqueTypes() throws Exception {
        unique(Arrays.asList(first, second, first));
    }

    @Test
    public void testToListNonList() throws Exception {
        List<String> list = toList(new HashSet<String>(Arrays.asList(FOO, BAR)));
        assertThat(list.size(), is(2));
        assertThat(list.contains(FOO), is(true));
        assertThat(list.contains(BAR), is(true));
    }

    @Test
    public void testToListList() throws Exception {
        List<String> original = Arrays.asList(FOO, BAR);
        List<String> list = toList(original);
        assertThat(list, sameInstance(original));
    }

    @Test
    public void testToListIterable() throws Exception {
        List<String> list = toList(new ArrayIterable(FOO, BAR));
        assertThat(list.size(), is(2));
        assertThat(list.contains(FOO), is(true));
        assertThat(list.contains(BAR), is(true));
    }

    @Test
    public void testToListIterableCollection() throws Exception {
        List<String> original = Arrays.asList(FOO, BAR);
        List<String> list = toList((Iterable<String>) original);
        assertThat(list, sameInstance(original));
    }

    @Test
    public void testConstructorIsHidden() throws Exception {
        assertThat(ByteBuddyCommons.class.getDeclaredConstructors().length, is(1));
        Constructor<?> constructor = ByteBuddyCommons.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers()), is(true));
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail();
        } catch (InvocationTargetException exception) {
            assertThat(exception.getCause().getClass(), CoreMatchers.<Class<?>>is(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testTypeIsFinal() throws Exception {
        assertThat(Modifier.isFinal(ByteBuddyCommons.class.getModifiers()), is(true));
    }

    private static class ArrayIterable implements Iterable<String> {

        private final String[] values;

        public ArrayIterable(String... values) {
            this.values = values;
        }

        @Override
        public Iterator<String> iterator() {
            return Arrays.asList(values).iterator();
        }
    }
}
