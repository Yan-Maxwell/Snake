package cn.edu.sustech.cs110.snake.events;

import cn.edu.sustech.cs110.snake.enums.GridState;
import cn.edu.sustech.cs110.snake.model.Position;
import java.util.Map;

public class BeanAteEvent {
    private final Map<Position, GridState> diff;

    public Map<Position, GridState> getDiff() {
        return this.diff;
    }

    public BeanAteEvent(final Map<Position, GridState> diff) {
        this.diff = diff;
    }

}
