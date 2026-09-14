package lumien.randomthings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

public class FertilizedDirtBlock extends Block {
    public static final BooleanProperty TILLED = BooleanProperty.create("tilled");
    private static final VoxelShape TILLED_SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public FertilizedDirtBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TILLED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TILLED) ? TILLED_SHAPE : super.getShape(state, level, pos, context);
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
        if (facing != Direction.UP) {
            return TriState.DEFAULT;
        }

        boolean crop = plant.is(BlockTags.CROPS);
        return state.getValue(TILLED) == crop ? TriState.TRUE : TriState.FALSE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos abovePos = pos.above();
        for (int i = 0; i < 3; i++) {
            BlockState above = level.getBlockState(abovePos);
            if (!(above.getBlock() instanceof BonemealableBlock) || !above.isRandomlyTicking()) {
                break;
            }
            above.randomTick(level, abovePos, random);
        }
    }
}
