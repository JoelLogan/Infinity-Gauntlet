package com.whitehallplugins.infinitygauntlet.items.gems.replicas;

public final class ReplicaGems {
    private ReplicaGems(){}
    public static final class MindGemReplica extends BaseGemReplica {
        public MindGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "mind";
        }
    }
    public static final class PowerGemReplica extends BaseGemReplica {
        public PowerGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "power";
        }
    }
    public static final class RealityGemReplica extends BaseGemReplica {
        public RealityGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "reality";
        }
    }
    public static final class SoulGemReplica extends BaseGemReplica {
        public SoulGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "soul";
        }
    }
    public static final class SpaceGemReplica extends BaseGemReplica {
        public SpaceGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "space";
        }
    }
    public static final class TimeGemReplica extends BaseGemReplica {
        public TimeGemReplica(Settings settings) {
            super(settings);
        }
        @Override
        protected String getGemType() {
            return "time";
        }
    }
}
