package com.github.terminatornl.tickcentral;

import com.github.terminatornl.tickcentral.asm.BlockTransformer;
import com.github.terminatornl.tickcentral.asm.EntityTransformer;
import com.github.terminatornl.tickcentral.asm.HubAPITransformer;
import com.github.terminatornl.tickcentral.asm.ITickableTransformer;
import com.github.terminatornl.tickcentral.asm.workarounds.Compatibility;
import com.github.terminatornl.tickcentral.core.Config;
import com.github.terminatornl.tickcentral.loading.Loader;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.ModAPITransformer;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({
		"com.github.terminatornl.tickcentral.asm.",
		"$wrapper.com.github.terminatornl.tickcentral.asm.",
		"com.github.terminatornl.tickcentral.loading.",
		"$wrapper.com.github.terminatornl.tickcentral.loading.",
		"com.github.terminatornl.tickcentral.TickCentral",
		"$wrapper.com.github.terminatornl.tickcentral.TickCentral",
		"com.github.terminatornl.tickcentral.core.Config",
		"$wrapper.com.github.terminatornl.tickcentral.core.Config",
		"com.github.terminatornl.tickcentral.core.ModContainer",
		"$wrapper.com.github.terminatornl.tickcentral.core.ModContainer",
		"com.github.terminatornl.tickcentral.api.ClassSniffer",
		"$wrapper.com.github.terminatornl.tickcentral.api.ClassSniffer",
})
@IFMLLoadingPlugin.Name(TickCentral.NAME)
@IFMLLoadingPlugin.SortingIndex(1001)
public class TickCentral implements IFMLLoadingPlugin, IFMLCallHook {
	public static final Config CONFIG = new Config();
	public static final String NAME = "TickCentral";
	public static final String MODID = "tickcentral";
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	public static final String VERSION = "${version}";
	public static final Loader LOADER = new Loader();
	public static TickCentral INSTANCE;
	public static Map<String, Object> FML_DATA;

	public TickCentral() {
		if(INSTANCE == null){
			INSTANCE = this;
			Compatibility.FixTransformerOrdering();
			LOGGER.info(TickCentral.NAME + " is initialized! Please ignore the warning about the missing MCVersion annotation, as this mod is intended to last across many Minecraft versions!");
		}else{
			LOGGER.debug(TickCentral.NAME + " is re-initialized.");
		}
	}

	/**
	 * Return a list of classes that implements the IClassTransformer interface
	 *
	 * @return a list of classes that implements the IClassTransformer interface
	 */
	@Override
	public String[] getASMTransformerClass() {
		List<String> list = LOADER.getAllClassTransformers();
		list.add(BlockTransformer.class.getName());
		list.add(ITickableTransformer.class.getName());
		list.add(EntityTransformer.class.getName());
		list.add(HubAPITransformer.class.getName());

		return list.toArray(new String[0]);
	}

	public Collection<Class<? extends IClassTransformer>> getPrioritizedASMTransformers() {
		List<Class<? extends IClassTransformer>> list = LOADER.getLastClassTransformersTypes();

		list.add(BlockTransformer.class);
		list.add(ITickableTransformer.class);
		list.add(EntityTransformer.class);
		list.add(HubAPITransformer.class);
		list.add(ModAPITransformer.class);

		return list;
	}

	@Override
	public String getModContainerClass() {
		return "com.github.terminatornl.tickcentral.core.ModContainer";
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	/**
	 * Inject coremod data into this coremod
	 * This data includes:
	 * "mcLocation" : the location of the minecraft directory,
	 * "coremodList" : the list of coremods
	 * "coremodLocation" : the file this coremod loaded from,
	 *
	 * @param data
	 */
	@Override
	public void injectData(Map<String, Object> data) {
		FML_DATA = data;
		LOADER.distributeInject(FML_DATA);
	}

	/**
	 * Return an optional access transformer class for this coremod. It will be injected post-deobf
	 * so ensure your ATs conform to the new srgnames scheme.
	 *
	 * @return the name of an access transformer class or null if none is provided
	 */
	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception if unable to compute a result
	 */
	@Override
	public Void call() throws Exception {
		LOADER.distributeCalls();
		return null;
	}
}

