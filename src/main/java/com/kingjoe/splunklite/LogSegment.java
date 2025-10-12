package com.kingjoe.splunklite;

import java.util.List;

public record LogSegment(String path, long from, long to) {
}