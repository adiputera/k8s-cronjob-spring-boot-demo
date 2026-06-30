package id.adiputera.demo.k8scron;

/**
 * @author Yusuf F. Adiputera
 */
public interface JobPerformable {
    void perform(String... args) throws Exception;
}
