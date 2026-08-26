public class ReportService {
    public Submission[] topKLargest(Submission[] all, int k) {
        if (all == null || all.length == 0 || k <= 0) return new Submission[0];
        int heapSize = Math.min(all.length, k);
        Submission[] minHeap = new Submission[heapSize];

        for (int i = 0; i < heapSize; i++) {
            minHeap[i] = all[i];
            siftUpMin(minHeap, i);
        }

        for (int i = k; i < all.length; i++) {
            if (all[i].getSizeKb() > minHeap[0].getSizeKb()) {
                minHeap[0] = all[i];
                siftDownMin(minHeap, 0, heapSize);
            }
        }
        return minHeap;
    }

    private void siftUpMin(Submission[] heap, int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].getSizeKb() < heap[parent].getSizeKb()) {
                Submission temp = heap[index];
                heap[index] = heap[parent];
                heap[parent] = temp;
                index = parent;
            } else break;
        }
    }

    private void siftDownMin(Submission[] heap, int index, int size) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && heap[left].getSizeKb() < heap[smallest].getSizeKb())
                smallest = left;
            if (right < size && heap[right].getSizeKb() < heap[smallest].getSizeKb())
                smallest = right;

            if (smallest != index) {
                Submission temp = heap[index];
                heap[index] = heap[smallest];
                heap[smallest] = temp;
                index = smallest;
            } else break;
        }
    }

    public Submission[] sortByTimeFast(Submission[] all) {
        Submission[] copy = new Submission[all.length];
        for(int i=0; i<all.length; i++) copy[i] = all[i];
        mergeSort(copy, 0, copy.length - 1);
        return copy;
    }

    private void mergeSort(Submission[] arr, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(Submission[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Submission[] L = new Submission[n1];
        Submission[] R = new Submission[n2];

        for (int i = 0; i < n1; ++i) L[i] = arr[left + i];
        for (int j = 0; j < n2; ++j) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i].getTimestampMs() <= R[j].getTimestampMs()) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    public Submission[] sortByTimeInsertion(Submission[] all) {
        Submission[] copy = new Submission[all.length];
        for(int i=0; i<all.length; i++) copy[i] = all[i];

        for (int i = 1; i < copy.length; i++) {
            Submission key = copy[i];
            int j = i - 1;
            while (j >= 0 && copy[j].getTimestampMs() > key.getTimestampMs()) {
                copy[j + 1] = copy[j];
                j = j - 1;
            }
            copy[j + 1] = key;
        }
        return copy;
    }

    public int findFirstAfter(Submission[] ascending, long deadlineMs) {
        int low = 0;
        int high = ascending.length - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (ascending[mid].getTimestampMs() > deadlineMs) {
                result = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return result;
    }

    public void printSheet(Submission[] ascending) {
        System.out.println(String.format("%-10s | %-25s | %-8s | %-12s | %s", "ID", "Dosya Adı", "Versiyon", "Zaman", "Durum"));
        System.out.println("-------------------------------------------------------------------------");
        for (Submission s : ascending) {
            System.out.println(String.format("%-10s | %-25s | v%-7d | %-12s | %s",
                    s.getStudentId(), s.getFileName(), s.getVersion(), s.clock(), (s.isLate() ? "LATE" : "ON TIME")));
        }
    }
}