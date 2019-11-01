package thedrake;

import java.util.ArrayList;
import java.util.List;

public class SlideAction extends TroopAction {


	public SlideAction(int offsetX, int offsetY) {
		super(offsetX, offsetY);
	}

	public SlideAction(Offset2D offset) {
		super(offset);
	}

	@Override
	public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
		List<Move> result = new ArrayList<>();
		TilePos target = origin.stepByPlayingSide(offset(), side);

		Offset2D tmp = offset();

		while (true) {
			if (state.canStep(origin, target)) {
				result.add(new StepOnly(origin, (BoardPos) target));
				tmp = new Offset2D(tmp.x + 1, offset().y);
				target = origin.stepByPlayingSide(tmp, side);
				continue;
			} else if (state.canCapture(origin, target)) {
				result.add(new StepAndCapture(origin, (BoardPos) target));
				tmp = new Offset2D(tmp.x + 1, offset().y);
				target = origin.stepByPlayingSide(tmp, side);
				continue;
			}
			break;
		}

		return result;

	}
}
