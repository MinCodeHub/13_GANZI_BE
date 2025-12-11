package site.talent_trade.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 설정 클래스
 */
@Configuration
public class AwsConfig {

  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.s3.region}")
  private String region;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .credentialsProvider(this::awsCredentials)
        .region(Region.of(region))
        .build();
  }

  private AwsCredentials awsCredentials() {
    return new AwsCredentials() {
      @Override
      public String accessKeyId() {
        return accessKey;
      }

      @Override
      public String secretAccessKey() {
        return secretKey;
      }
    };
  }
}
