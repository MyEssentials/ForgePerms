package forgeperms.asm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * Patching system. Mostly copied from https://github.com/matthewprenger/ServerTools/blob/develop/src/main/java/com/matthewprenger/servertools/core/asm/STClassTransformer.java
 */
public class FPClassTransformer implements IClassTransformer {
	private static FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
    private static final Set<PatchNote> patches = new HashSet<PatchNote>();

    static {
        PatchNote chPatch = new PatchNote("net.minecraft.command.CommandHandler", "forgeperms.asm.FPCommandHandler");
        chPatch.addMethodToPatch(new MethodNote("executeCommand", "func_71556_a", "(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)I"));
        chPatch.addMethodToPatch(new MethodNote("getPossibleCommands", "func_71558_b", "(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;"));
        chPatch.addMethodToPatch(new MethodNote("getPossibleCommands", "func_71557_a", "(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;"));
        addPatch(chPatch);
    }
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null)
            return null;

        for (PatchNote patchNote : patches) {
            if (patchNote.sourceClass.equals(transformedName)) {
                return transform(name, patchNote, bytes);
            }
        }

        return bytes;
	}

    private static byte[] transform(String obfName, PatchNote patchNote, byte[] bytes) {

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        if (patchNote.methodsToPatch.isEmpty())
            return bytes;

        for (MethodNote methodNote : patchNote.methodsToPatch) {

            MethodNode sourceMethod = null;
            MethodNode replacementMethod = null;

            try {

                for (MethodNode method : classNode.methods) {
                    if (methodNote.srgMethodName.equals(remapper.mapMethodName(obfName, method.name, method.desc))) {
                        sourceMethod = method;
                        break;
                    } else if (methodNote.methodName.equals(method.name) && methodNote.deobfDesc.equals(method.desc)) {
                        sourceMethod = method;
                    }
                }

                ClassNode replacementClass = loadClass(patchNote.replacementClass);
                for (MethodNode method : replacementClass.methods) {
                    if (methodNote.srgMethodName.equals(remapper.mapMethodName(patchNote.replacementClass, method.name, method.desc))) {
                        replacementMethod = method;
                        break;
                    } else if (methodNote.methodName.equals(method.name) && methodNote.deobfDesc.equals(method.desc)) {
                        replacementMethod = method;
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }

            if (sourceMethod != null && replacementMethod != null) {
            	System.out.println("[ForgePermsCore] Successfully Mapped Method to be Replaced");
            	System.out.println(String.format("    Source: %s@%s Replacement: %s@%s", sourceMethod.name, sourceMethod.desc, replacementMethod.name, replacementMethod.desc));
                classNode.methods.remove(sourceMethod);
                classNode.methods.add(replacementMethod);

            } else {
                return bytes;
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    public static void addPatch(PatchNote patchNote) {
        patches.add(patchNote);
    }
    
    private static ClassNode loadClass(String className) throws IOException {
        LaunchClassLoader loader = (LaunchClassLoader) FPClassTransformer.class.getClassLoader();
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(loader.getClassBytes(className));
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static class PatchNote {
        public final String sourceClass;
        public final String replacementClass;

        public final Set<MethodNote> methodsToPatch = new HashSet<MethodNote>();

        public PatchNote(String sourceClass, String replacementClass) {
            this.sourceClass = sourceClass;
            this.replacementClass = replacementClass;
        }

        public void addMethodToPatch(MethodNote methodNote) {

            methodsToPatch.add(methodNote);
        }
    }

    public static class MethodNote {

        public final String methodName;
        public final String srgMethodName;
        public final String deobfDesc;

        public MethodNote(String methodName, String srgMethodName, String deobfDesc) {

            this.methodName = methodName;
            this.srgMethodName = srgMethodName;
            this.deobfDesc = deobfDesc;
        }
    }
}