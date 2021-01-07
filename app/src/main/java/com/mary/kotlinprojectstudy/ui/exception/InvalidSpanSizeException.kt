package com.mary.kotlinprojectstudy.ui.exception

class InvalidSpanSizeException(errorSize: Int, maxSpanSize: Int) :
    RuntimeException("Invalid item span size: $errorSize. Span size must be in the range: (1...$maxSpanSize)")