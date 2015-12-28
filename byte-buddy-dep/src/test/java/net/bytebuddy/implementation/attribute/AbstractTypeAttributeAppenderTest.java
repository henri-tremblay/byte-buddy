package net.bytebuddy.implementation.attribute;

import net.bytebuddy.description.type.TypeDescription;
import org.junit.Before;
import org.mockito.Answers;
import org.mockito.Mock;
import org.objectweb.asm.ClassVisitor;

import static org.mockito.Mockito.when;

public abstract class AbstractTypeAttributeAppenderTest extends AbstractAttributeAppenderTest {

    @Mock(answer = Answers.RETURNS_MOCKS)
    protected ClassVisitor classVisitor;

    @Mock
    protected TypeDescription rawTypeDescription;

    @Mock
    protected TypeDescription.Generic typeDescription;

    @Mock
    protected TypeDescription.Generic targetType;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(rawTypeDescription.asGenericType()).thenReturn(typeDescription);
    }
}
