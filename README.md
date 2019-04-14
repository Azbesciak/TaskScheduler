# TaskScheduler
Algorithm for task scheduling on single machine

### How to run
it is commandline application - you run in 2 ways this by passing arguments:
- 0/1 args - interactive mode; First arg is path to instance files - if not set, you will be asked later.
- 2/3 args - batch mode with args: `<path to problem file> [problem ids ',' separated] <h values ',' separated>`
     

### Allowed properties
Passed as JVM properties (with `-d` as follows `<param>=<value>)
| name | type | default | note |
| ---- | ---- | ------- | ---- |
| `benchmark` | `int` | 25 | used in benchmark mode, sets iteration count |
| `stopCondition.maxSolutions` | `int` |100 | max solutions made by solver |
| `stopCondition.notImprovingSolutions` | `int` | 10 | limit when solver won't continue solving |
| `stopCondition.timeLimit` | `string` | 1s | target solving time; checked only between iterations |
| `outputDir` | `string` | StdOutput | path where the result will be returned; if not set - `StdOutput` |
| `details` | `boolean` | `false` - solver, `true` - benchmark | whether instance details should be printed |
| `measureTime` | `boolean` | `false` - solver, `true` - benchmark | should measure time |
