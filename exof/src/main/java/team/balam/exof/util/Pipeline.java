package team.balam.exof.util;

public class Pipeline {
	private PipeExecutor first;
	private PipeExecutor last;

	public Object execute(Object in) {
		return this.first.executeAndMoveNext(in);
	}

	public Pipeline add(Pipe pipe) {
		if (this.first == null) {
			this.first = new PipeExecutor(pipe);
			this.last = this.first;
		} else {
			this.last = this.last.next(pipe);
		}
		return this;
	}


	public class PipeExecutor {
		private Pipe myPipe;
		private PipeExecutor next;

		PipeExecutor(Pipe pipe) {
			this.myPipe = pipe;
		}

		private PipeExecutor next(Pipe pipe) {
			this.next = new PipeExecutor(pipe);
			return this.next;
		}

		private Object executeAndMoveNext(Object in) {
			Object out = this.myPipe.execute(in);
			if (this.myPipe.isStop()) {
				return out;
			}

			if (this.next == null) {
				return out;
			} else {
				return this.next.executeAndMoveNext(out);
			}
		}
	}

	public interface Pipe {
		Object execute(Object in);

		default boolean isStop() {
			return false;
		}
	}
}
