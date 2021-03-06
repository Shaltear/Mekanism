package mekanism.generators.client;

import java.io.IOException;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.ctm.CTMRegistry;
import mekanism.generators.client.gui.GuiBioGenerator;
import mekanism.generators.client.gui.GuiGasGenerator;
import mekanism.generators.client.gui.GuiHeatGenerator;
import mekanism.generators.client.gui.GuiIndustrialTurbine;
import mekanism.generators.client.gui.GuiNeutronCapture;
import mekanism.generators.client.gui.GuiReactorController;
import mekanism.generators.client.gui.GuiReactorFuel;
import mekanism.generators.client.gui.GuiReactorHeat;
import mekanism.generators.client.gui.GuiReactorLogicAdapter;
import mekanism.generators.client.gui.GuiReactorStats;
import mekanism.generators.client.gui.GuiSolarGenerator;
import mekanism.generators.client.gui.GuiTurbineStats;
import mekanism.generators.client.gui.GuiWindGenerator;
import mekanism.generators.client.render.RenderAdvancedSolarGenerator;
import mekanism.generators.client.render.RenderBioGenerator;
import mekanism.generators.client.render.RenderGasGenerator;
import mekanism.generators.client.render.RenderHeatGenerator;
import mekanism.generators.client.render.RenderIndustrialTurbine;
import mekanism.generators.client.render.RenderReactor;
import mekanism.generators.client.render.RenderSolarGenerator;
import mekanism.generators.client.render.RenderTurbineRotor;
import mekanism.generators.client.render.RenderWindGenerator;
import mekanism.generators.client.render.item.GeneratorItemModelFactory;
import mekanism.generators.common.GeneratorsBlocks;
import mekanism.generators.common.GeneratorsCommonProxy;
import mekanism.generators.common.GeneratorsItems;
import mekanism.generators.common.block.states.BlockStateGenerator.GeneratorBlockStateMapper;
import mekanism.generators.common.block.states.BlockStateGenerator.GeneratorType;
import mekanism.generators.common.block.states.BlockStateReactor.ReactorBlockStateMapper;
import mekanism.generators.common.block.states.BlockStateReactor.ReactorBlockType;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import mekanism.generators.common.tile.TileEntityWindGenerator;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorNeutronCapture;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;
import mekanism.generators.common.tile.turbine.TileEntityTurbineVent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GeneratorsClientProxy extends GeneratorsCommonProxy
{
	public static final String[] CUSTOM_RENDERS = new String[] {"heat_generator", "solar_generator", "bio_generator", "wind_generator",
		"gas_generator", "advanced_solar_generator"};
	
	private static final IStateMapper generatorMapper = new GeneratorBlockStateMapper();
	private static final IStateMapper reactorMapper = new ReactorBlockStateMapper();
	
	@Override
	public void registerSpecialTileEntities()
	{
		ClientRegistry.registerTileEntity(TileEntityAdvancedSolarGenerator.class, "AdvancedSolarGenerator", new RenderAdvancedSolarGenerator());
		ClientRegistry.registerTileEntity(TileEntitySolarGenerator.class, "SolarGenerator", new RenderSolarGenerator());
		ClientRegistry.registerTileEntity(TileEntityBioGenerator.class, "BioGenerator", new RenderBioGenerator());
		ClientRegistry.registerTileEntity(TileEntityHeatGenerator.class, "HeatGenerator", new RenderHeatGenerator());
		ClientRegistry.registerTileEntity(TileEntityGasGenerator.class, "GasGenerator", new RenderGasGenerator());
		ClientRegistry.registerTileEntity(TileEntityWindGenerator.class, "WindTurbine", new RenderWindGenerator());
		ClientRegistry.registerTileEntity(TileEntityReactorController.class, "ReactorController", new RenderReactor());
		ClientRegistry.registerTileEntity(TileEntityTurbineRotor.class, "TurbineRod", new RenderTurbineRotor());
		ClientRegistry.registerTileEntity(TileEntityTurbineCasing.class, "TurbineCasing", new RenderIndustrialTurbine());
		ClientRegistry.registerTileEntity(TileEntityTurbineValve.class, "TurbineValve", new RenderIndustrialTurbine());
		ClientRegistry.registerTileEntity(TileEntityTurbineVent.class, "TurbineVent", new RenderIndustrialTurbine());
	}

	@Override
	public void registerItemRenders()
	{
		registerItemRender(GeneratorsItems.SolarPanel);
		registerItemRender(GeneratorsItems.Hohlraum);
		registerItemRender(GeneratorsItems.TurbineBlade);
	}
	
	@Override
	public void registerBlockRenders()
	{
		ModelLoader.setCustomStateMapper(GeneratorsBlocks.Generator, generatorMapper);
		ModelLoader.setCustomStateMapper(GeneratorsBlocks.Reactor, reactorMapper);
		ModelLoader.setCustomStateMapper(GeneratorsBlocks.ReactorGlass, reactorMapper);
		
		for(GeneratorType type : GeneratorType.values())
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(type.blockType.getBlock()), type.meta, new ModelResourceLocation("mekanismgenerators:" + type.getName(), "inventory"));
		}
		
		for(ReactorBlockType type : ReactorBlockType.values())
		{
			if(type.isValidReactorBlock())
			{
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(type.blockType.getBlock()), type.meta, new ModelResourceLocation("mekanismgenerators:" + type.getName(), "inventory"));
			}
		}
	}
	
	public void registerItemRender(Item item)
	{
		MekanismRenderer.registerItemRender("mekanismgenerators", item);
	}
	
	@SubscribeEvent
    public void onModelBake(ModelBakeEvent event) throws IOException 
    {
		for(String s : CUSTOM_RENDERS)
		{
			ModelResourceLocation model = new ModelResourceLocation("mekanismgenerators:" + s, "inventory");
	        Object obj = event.getModelRegistry().getObject(model);
	        
	        if(obj instanceof IBakedModel)
	        {
	        	event.getModelRegistry().putObject(model, new GeneratorItemModelFactory((IBakedModel)obj));
	        }
		}
    }
	
	@Override
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(this);
		
		CTMRegistry.registerCTMs("mekanismgenerators", "turbine_vent", "turbine_valve", "turbine_casing", "electromagnetic_coil", 
				"reactor_controller", "reactor_frame", "reactor_port", "reactor_port_output", "reactor_glass", "laser_focus_matrix", 
				"reactor_logic_adapter", "reactor_controller_on", "saturating_condenser");
	}

	@Override
	public GuiScreen getClientGui(int ID, EntityPlayer player, World world, BlockPos pos)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		switch(ID)
		{
			case 0:
				return new GuiHeatGenerator(player.inventory, (TileEntityHeatGenerator)tileEntity);
			case 1:
				return new GuiSolarGenerator(player.inventory, (TileEntitySolarGenerator)tileEntity);
			case 3:
				return new GuiGasGenerator(player.inventory, (TileEntityGasGenerator)tileEntity);
			case 4:
				return new GuiBioGenerator(player.inventory, (TileEntityBioGenerator)tileEntity);
			case 5:
				return new GuiWindGenerator(player.inventory, (TileEntityWindGenerator)tileEntity);
			case 6:
				return new GuiIndustrialTurbine(player.inventory, (TileEntityTurbineCasing)tileEntity);
			case 7:
				return new GuiTurbineStats(player.inventory, (TileEntityTurbineCasing)tileEntity);
			case 10:
				return new GuiReactorController(player.inventory, (TileEntityReactorController)tileEntity);
			case 11:
				return new GuiReactorHeat(player.inventory, (TileEntityReactorController)tileEntity);
			case 12:
				return new GuiReactorFuel(player.inventory, (TileEntityReactorController)tileEntity);
			case 13:
				return new GuiReactorStats(player.inventory, (TileEntityReactorController)tileEntity);
			case 14:
				return new GuiNeutronCapture(player.inventory, (TileEntityReactorNeutronCapture)tileEntity);
			case 15:
				return new GuiReactorLogicAdapter(player.inventory, (TileEntityReactorLogicAdapter)tileEntity);
		}
		
		return null;
	}
	
	@SubscribeEvent
	public void onStitch(TextureStitchEvent.Pre event)
	{
		RenderIndustrialTurbine.resetDisplayInts();
	}
}
