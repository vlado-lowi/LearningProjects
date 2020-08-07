class ClassCountingInstances {

    private static long numberOfInstances = 0;

    public ClassCountingInstances() {
        synchronized (ClassCountingInstances.class) {
            numberOfInstances++;
        }
    }

    public static synchronized long getNumberOfInstances() {
        return numberOfInstances;
    }
}