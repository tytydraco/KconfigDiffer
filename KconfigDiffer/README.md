# KconfigDiffer
Usage: <INITIAL_CONFIG> <CONFIG_DIFF> <OUTPUT_CONFIG>

Your main config file goes in the first arg.

The second arg is basically a diff of the config. 
Example:
CONFIG_BALANCE_ANON_FILE_RECLAIM=y
\# CONFIG_ZRAM is not set

This diff would enable anon file reclaim, and disable ZRAM.

The last arg is the output config with the changes applied.
