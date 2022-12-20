#!/bin/bash
oggdec $1.ogg ; oggenc $1.wav ; rm $1.wav