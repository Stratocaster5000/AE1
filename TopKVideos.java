
import java.util.ArrayList;
import java.util.List;

public class TopKVideos {

    static class Video {

        String id;
        int views;

        Video(String id, int views) {
            this.id = id;
            this.views = views;
        }
    }

    public static List<Video> findTopKVideos(List<Video> videos, int k) {
        int n = videos.size();

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(videos, n, i);
        }

        // Extract top k elements
        List<Video> topK = new ArrayList<>();
        for (int i = 0; i < k && i < n; i++) {
            topK.add(videos.get(0));
            videos.set(0, videos.get(n - 1 - i));
            heapify(videos, n - 1 - i, 0);
        }

        return topK;
    }

    private static void heapify(List<Video> videos, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && videos.get(left).views > videos.get(largest).views) {
            largest = left;
        }

        if (right < n && videos.get(right).views > videos.get(largest).views) {
            largest = right;
        }

        if (largest != i) {
            Video swap = videos.get(i);
            videos.set(i, videos.get(largest));
            videos.set(largest, swap);
            heapify(videos, n, largest);
        }
    }

    public static void main(String[] args) {
        List<Video> videos = new ArrayList<>();
        videos.add(new Video("vid1", 100));
        videos.add(new Video("vid2", 200));
        videos.add(new Video("vid3", 50));
        videos.add(new Video("vid4", 300));
        videos.add(new Video("vid5", 75));

        int k = 3;
        List<Video> topK = findTopKVideos(videos, k);

        System.out.println("Top " + k + " videos:");
        for (Video v : topK) {
            System.out.println("Video ID: " + v.id + ", Views: " + v.views);
        }
    }
}
